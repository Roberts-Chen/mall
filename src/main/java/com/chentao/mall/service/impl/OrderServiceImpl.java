package com.chentao.mall.service.impl;

import com.chentao.mall.enums.OrderStatusEnum;
import com.chentao.mall.enums.PaymentTypeEnum;
import com.chentao.mall.enums.ProductStatusEnum;
import com.chentao.mall.enums.ResponseEnum;
import com.chentao.mall.mapper.OrderItemMapper;
import com.chentao.mall.mapper.OrderMapper;
import com.chentao.mall.mapper.ProductMapper;
import com.chentao.mall.mapper.ShippingMapper;
import com.chentao.mall.pojo.*;
import com.chentao.mall.service.ICartService;
import com.chentao.mall.service.IOrderService;
import com.chentao.mall.vo.OrderItemVo;
import com.chentao.mall.vo.OrderVo;
import com.chentao.mall.vo.ResponseVo;
import com.chentao.mall.vo.ShippingVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private ICartService cartService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {
        // 收货地址校验（查出来）
        Shipping shipping = shippingMapper.selectByIdAndUid(uid, shippingId);
        if (shipping == null) {
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }

        // 根据uid获取购物车
        List<Cart> cartList = cartService.listForCart(uid).stream()
                .filter(Cart::getProductSelected)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cartList)) {
            return ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }

        // 获取cartList的productIdSet
        Set<Integer> productIdSet = cartList.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toSet());
        Map<Integer, Product> productMap = productMapper.selectByProductIdSet(productIdSet);

        List<OrderItem> orderItemList = new ArrayList<>();
        Long orderNo = generateOrderNo();
        for (Cart cart : cartList) {
            Product product = productMap.get(cart.getProductId());
            // 是否有该商品
            if (product == null) {
                return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST,
                        "商品不存在. productId=" + cart.getProductId());
            }

            // 商品上下架状态
            if (!ProductStatusEnum.ON_SALE.getCode().equals(product.getStatus())) {
                return ResponseVo.error(ResponseEnum.PRODUCT_OF_SALE_OR_DELETE,
                        "商品处于非在售状态. " + product.getName());
            }

            // 库存是否充足
            if (product.getStock() < 0) {
                return ResponseVo.error(ResponseEnum.STOCK_ERROR,
                        "商品库存不足. " + product.getName());
            }

            OrderItem orderItem = buildOrderItem(uid, orderNo, cart.getQuantity(), product);
            orderItemList.add(orderItem);

            // 减库存，并更新到数据库中
            product.setStock(product.getStock() - cart.getQuantity());
            int row = productMapper.updateByPrimaryKeySelective(product);
            if (row < 0) {
                return ResponseVo.error(ResponseEnum.ERROR);
            }


        }

        // 计算总价（只计算选中的商品）
        // 生成订单：写数据，写入order表和order_item表，利用事务
        Order order = buildOrder(uid, orderNo, shippingId, orderItemList);

        int row1 = orderMapper.insertSelective(order);
        if (row1 <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        int row2 = orderItemMapper.batchInsert(orderItemList);
        if (row2 <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }



        // 更新购物车（只更新选中商品）
        // 更新购物车为啥要放在外面？因为Redis虽然有事务（伪事务），但是不能回滚
        // 所谓更新购物车就是删除购物车中的商品项，如果边遍历边删除，当出现运行时错误时，被删除的商品项已经不能再恢复了
        for (Cart cart : cartList) {
            cartService.delete(uid, cart.getProductId());
        }

        // 构造OrderVo，返回给前端
        return ResponseVo.success(buildOrderVo(order, orderItemList, shipping));
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUid(uid);

        Set<Long> orderNoSet = orderList.stream()
                .map(Order::getOrderNo)
                .collect(Collectors.toSet());
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);


        Set<Integer> shippingIdSet = orderList.stream()
                .map(Order::getShippingId)
                .collect(Collectors.toSet());
        List<Shipping> shippingList = shippingMapper.selectByIdSet(shippingIdSet);


        // 以OrderNo作为键，以OrderNo对应的orderItem列表作为值
        Map<Long, List<OrderItem>> orderItemMap = orderItemList.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderNo));

        // 以shippingId作为key值，以shipping作为value值
        Map<Integer, Shipping> shippingMap = shippingList.stream()
                .collect(Collectors.toMap(Shipping::getId, shipping -> shipping));

        List<OrderVo> orderVoList = new ArrayList<>();
        for (Order order : orderList) {
            OrderVo orderVo = buildOrderVo(order, orderItemMap.get(order.getOrderNo()), shippingMap.get(order.getShippingId()));
            orderVoList.add(orderVo);
        }

        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVoList);
        return ResponseVo.success(pageInfo);
    }

    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order, orderVo);

        List<OrderItemVo> orderItemVoList = orderItemList.stream()
                .map(e -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    BeanUtils.copyProperties(e, orderItemVo);
                    return orderItemVo;
                })
                .collect(Collectors.toList());
        orderVo.setOrderItemVoList(orderItemVoList);

        ShippingVo shippingVo = new ShippingVo();
        BeanUtils.copyProperties(shipping, shippingVo);
        orderVo.setShippingVo(shippingVo);

        return orderVo;
    }

    /**
     * 简单的订单id
     * 企业级：分布式唯一id
     * @return
     */
    private Long generateOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(999);
    }

    private OrderItem buildOrderItem(Integer uid, Long orderNo, Integer quantity, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(uid);
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImages());
        orderItem.setCurrentUnitPrice(product.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return orderItem;
    }

    // 创建订单
    private Order buildOrder(Integer uid,
                             Long orderNo,
                             Integer shippingId,
                             List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        order.setUserId(uid);
        order.setShippingId(shippingId);
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setPostage(0);
        order.setPayment(orderItemList.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return order;
    }
}
