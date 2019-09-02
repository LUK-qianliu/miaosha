package com.qianliu.demo.service;

import com.qianliu.demo.dao.GoodsDAO;
import com.qianliu.demo.domain.MiaoshaGoods;
import com.qianliu.demo.vo.GoodsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GoodsService {

    @Resource
    GoodsDAO goodsDAO;

    /**
     * 查询goods和miaosha_goods
     * @return
     */
    public List<GoodsVo> listGoodsVo(){
        return goodsDAO.listGoodsVo();
    }

    /**
     * 通过id查询goodsVo
     * @param goodsId
     * @return
     */
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDAO.getGoodsVo(goodsId);
    }

    /**
     * 减少库存
     * @param goods
     */
    public void reduceStock(GoodsVo goods) {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        goodsDAO.reduceStock(g);
    }

}
