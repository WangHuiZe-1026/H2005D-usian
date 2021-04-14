package com.usian.service;


import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemParamService {

    @Autowired
    private TbItemParamMapper tbItemParamMapper;

    public TbItemParam selectItemParamByItemCatId(Long itemCatId) {
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = tbItemParamExample.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);
        List<TbItemParam> list = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);
        if(list!=null && list.size()>0){
                return list.get(0);
        }
        return null;
    }
}
