package com.qianliu.demo.service;

import com.qianliu.demo.domain.MiaoshaOrder;
import com.qianliu.demo.domain.MiaoshaUser;
import com.qianliu.demo.domain.OrderInfo;
import com.qianliu.demo.domain.User;
import com.qianliu.demo.redis.RedisService;
import com.qianliu.demo.redis.domain.impl.MiaoshaKey;
import com.qianliu.demo.util.MD5Util;
import com.qianliu.demo.util.UUIDUtil;
import com.qianliu.demo.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;


@Service
public class MiaoshaService {

    private Logger logger = LoggerFactory.getLogger(MiaoshaService.class);
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService  redisService;

    /**
     * 秒杀事务
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存 下订单 写入秒杀订单
        Boolean stock_bool = goodsService.reduceStock(goods);
        //order_info maiosha_order
        if(stock_bool==true){
            return orderService.createOrder(user, goods);
        }else {
            setGoodsOver(goods.getId());// 设置秒杀的商品出售完了
            return null;
        }
    }

    /**
     * orderID:秒杀成功
     * -1：秒杀失败
     * 0： 排队中
     */
    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        logger.info("秒杀订单:"+miaoshaOrder);
        if(miaoshaOrder!=null){
            return miaoshaOrder.getId();
        }else {
            Boolean is_over = getGoodsOver(goodsId);
            if(is_over){
                return -1;
            }else {
                return 0;
            }
        }
    }

    /**
     * 在redis中设置秒杀商品是否秒杀成功
     * @param goodsId
     */
    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
    }

    /**
     * 在redis中获取秒杀商品是否秒杀成功
     * @param goodsId
     */
    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
    }

    /**
     * 从redis中检查验证码是否正确
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    public Boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if(user==null||path==null){
            return false;
        }

        //从redis中拿出path验证
        String path_redis = redisService.get(MiaoshaKey.getMiaoshaPath,user.getId()+"_"+goodsId,String.class);
        if(path_redis.equals(path)){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 生成path放入redis
     * @param user
     * @param goodsId
     * @return
     */
    public String createPath(MiaoshaUser user, long goodsId) {
        String path = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath,user.getId()+"_"+goodsId,path);
        return path;
    }

    /**
     * 生成验证码
     * @param user
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;

        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);// 背景颜色的填充

        // draw the border
        g.setColor(Color.black); //黑色矩形框
        g.drawRect(0, 0, width - 1, height - 1);

        // create a random instance to generate the codes
        Random rdm = new Random();

        // make some confusion：设置50个干扰的点
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }

        // generate a random code：把rdm数字绘图到画板上
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();

        //把验证码存到redis中
        int rnd = calc(verifyCode);// 把数学公式计算出来
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);

        //输出图片
        return image;
    }

    private static char[] ops = new char[] {'+', '-', '*'};

    /**
     * 验证码：生成计算验证码的公式
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    /**
     * 验证码：计算表达式中的结果
     * @param exp
     * @return
     */
    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 验证码：校验验证码
     * @param user
     * @param goodsId
     * @param verifyCode
     */
    public Boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
        if (user == null || goodsId <= 0) {
            return false;
        }

        // 从redis中取出验证码数据进行对比
        Integer verifyCode_redis = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if (verifyCode_redis == null|| verifyCode_redis != verifyCode ) {
            return false;
        }

        // 删除redis中的验证码
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId);

        return true;
    }

}
