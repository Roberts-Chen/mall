package com.chentao.mall.service.impl;

import com.chentao.mall.enums.ProductStatusEnum;
import com.chentao.mall.enums.ResponseEnum;
import com.chentao.mall.form.CartAddForm;
import com.chentao.mall.form.CartUpdateForm;
import com.chentao.mall.mapper.ProductMapper;
import com.chentao.mall.pojo.Cart;
import com.chentao.mall.pojo.Product;
import com.chentao.mall.service.ICartService;
import com.chentao.mall.vo.CartProductVo;
import com.chentao.mall.vo.CartVo;
import com.chentao.mall.vo.ResponseVo;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class CartServiceImpl implements ICartService {

    private static final String CART_REDIS_KEY_TEMPLATE = "cart_%d";

    private Gson gson = new Gson();

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 添加商品到购物车
     *
     * @param form
     * @return
     */
    @Override
    public ResponseVo<CartVo> add(Integer uid, CartAddForm form) {
        Product product = productMapper.selectByPrimaryKey(form.getProductId());
        int quantity = 1;
        // 判断商品是否存在
        if (product == null) {
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
        }

        // 商品是否正常在售
        if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())) {
            return ResponseVo.error(ResponseEnum.PRODUCT_OF_SALE_OR_DELETE);
        }

        // 商品库存是否充足
        if (product.getStock() <= 0) {
            return ResponseVo.error(ResponseEnum.STOCK_ERROR);
        }

        // 写入数据到Redis，使用jackson将cart对象转换为json字符串
        // 使用redis的hash数据结构来存储购物车，对于购物车中存在的商品来说，每存一次，就将数量累加1
        // 用productId作为hash的键
        HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String value = opsForHash.get(redisKey, String.valueOf(product.getId()));

        Cart cart = null;
        if (StringUtils.hasText(value)) {
            // 如果Redis中存在商品，数量+1
            cart = gson.fromJson(value, Cart.class);
            cart.setQuantity(cart.getQuantity() + quantity);
        } else {
            // 如果Redis中不存在，新增
            cart = new Cart(product.getId(), quantity, form.getSelected());
        }
        opsForHash.put(redisKey, String.valueOf(product.getId()), gson.toJson(cart));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        CartVo cartVo = new CartVo();

        List<CartProductVo> cartProductVoList = new ArrayList<>();
        boolean selectAll = true;
        Integer cartTotalQuantity = 0;
        BigDecimal cartTotalPrice = BigDecimal.ZERO;


        HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);


//        for (Map.Entry<String, String> entry : entries.entrySet()) {
//            Integer productId = Integer.valueOf(entry.getKey());
//            Cart cart = null;
//            try {
//                cart = mapper.readValue(entry.getValue(), Cart.class);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            Product product = productMapper.selectByPrimaryKey(productId);
//            if (product != null) {
//                CartProductVo cartProductVo = new CartProductVo(productId,
//                        cart.getQuantity(),
//                        product.getName(),
//                        product.getSubtitle(),
//                        product.getMainImages(),
//                        product.getPrice(),
//                        product.getStatus(),
//                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
//                        product.getStock(),
//                        cart.getProductSelected()
//                );
//
//                if (!cart.getProductSelected()) {
//                    selectAll = false;
//                }
//
//                cartProductVoList.add(cartProductVo);
//                cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
//            }
//            cartTotalQuantity += cart.getQuantity();
//        }


        Set<Integer> productIdSet = new HashSet<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Integer productId = Integer.valueOf(entry.getKey());
            productIdSet.add(productId);
        }

        Map<Integer, Product> products = productMapper.selectByProductIdSet(productIdSet);

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Integer productId = Integer.valueOf(entry.getKey());

            Cart cart = gson.fromJson(entry.getValue(), Cart.class);

            Product product = products.get(productId);
            if (product != null) {
                CartProductVo cartProductVo = new CartProductVo(productId,
                        cart.getQuantity(),
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImages(),
                        product.getPrice(),
                        product.getStatus(),
                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                        product.getStock(),
                        cart.getProductSelected()
                );

                if (!cart.getProductSelected()) {
                    selectAll = false;
                }

                cartProductVoList.add(cartProductVo);
                cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
            }
            cartTotalQuantity += cart.getQuantity();
        }

        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        cartVo.setSelectAll(selectAll);
        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form) {
        HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (!StringUtils.hasText(value)) {
            // 如果Redis购物车中不存在该商品，报错
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }

        // 如果Redis中存在商品，修改其数据
        Cart cart = null;
//        cart = mapper.readValue(value, Cart.class);
        cart = gson.fromJson(value, Cart.class);
        // 数量不为空，且大于等于0
        if (form.getQuantity() != null
                && form.getQuantity() >= 0) {
            cart.setQuantity(form.getQuantity());
        }
        if (form.getSelected() != null) {
            cart.setProductSelected(form.getSelected());
        }

        // 重新添加到Redis中去
        opsForHash.put(redisKey, String.valueOf(productId), gson.toJson(cart));


        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (!StringUtils.hasText(value)) {
            // 如果Redis购物车中不存在该商品，报错
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }
        // 从Redis中删除
        opsForHash.delete(redisKey, String.valueOf(productId));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(true);
            opsForHash.put(redisKey, String.valueOf(cart.getProductId()), gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> unSelectAll(Integer uid) {
        HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(false);
            opsForHash.put(redisKey, String.valueOf(cart.getProductId()), gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        return ResponseVo.success(listForCart(uid).stream()
                .map(Cart::getQuantity)
                .reduce(0, Integer::sum)
        );
    }

    public List<Cart> listForCart(Integer uid) {
        HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        List<Cart> cartList = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cartList.add(gson.fromJson(entry.getValue(), Cart.class));
        }
        return cartList;
    }
}
