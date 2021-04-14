package com.usian.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemCatMapper;
import com.usian.mapper.TbItemDescMapper;
import com.usian.mapper.TbItemMapper;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.pojo.*;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    public TbItem getById(Long itemId){
        return tbItemMapper.selectByPrimaryKey(itemId);
    }

    public PageResult selectTbItemAllByPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        //查询状态是1 并且  按修改时间逆序排列
        TbItemExample tbItemExample = new TbItemExample();
        tbItemExample.setOrderByClause("updated DESC");
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andStatusEqualTo((byte)1);
        List<TbItem> tbItemList = tbItemMapper.selectByExample(tbItemExample);
        for (int i = 0; i < tbItemList.size(); i++) {
            TbItem tbItem =  tbItemList.get(i);
            tbItem.setPrice(tbItem.getPrice()/100);

        }
        PageInfo<TbItem> tbItemPageInfo = new PageInfo<>(tbItemList);
        //返回PageResult
        PageResult pageResult = new PageResult();
        pageResult.setResult(tbItemPageInfo.getList());
        pageResult.setTotalPage(Long.valueOf(tbItemPageInfo.getPages()));
        pageResult.setPageIndex(tbItemPageInfo.getPageNum());
        return pageResult;
    }

    public Integer insertTbItem(TbItem tbItem, String desc, String itemParams) {
        long itemId = IDUtils.genItemId();
        Date date = new Date();
        tbItem.setId(itemId);
        tbItem.setStatus((byte)1);
        tbItem.setUpdated(date);
        tbItem.setCreated(date);
        tbItem.setPrice(tbItem.getPrice()*100);
        int tbItemNum = tbItemMapper.insertSelective(tbItem);

        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setUpdated(date);
        tbItemDesc.setCreated(date);
        int tbItemDescNum = tbItemDescMapper.insertSelective(tbItemDesc);

        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setItemId(itemId);
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setCreated(date);
        tbItemParamItem.setUpdated(date);
        int itemParamItemNum = tbItemParamItemMapper.insertSelective(tbItemParamItem);

        return tbItemNum+tbItemDescNum+itemParamItemNum;

    }

    public Map<String, Object> preUpdateItem(Long itemId) {
        Map<String, Object> map = new HashMap<>();
        TbItem tbItem = this.tbItemMapper.selectByPrimaryKey(itemId);
        map.put("item",tbItem);
        TbItemDesc itemDesc = this.tbItemDescMapper.selectByPrimaryKey(itemId);
        map.put("itemDesc", itemDesc.getItemDesc());
        TbItemCat itemCat = this.tbItemCatMapper.selectByPrimaryKey(tbItem.getCid());
        map.put("itemCat", itemCat.getName());
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        List<TbItemParamItem> list =
                this.tbItemParamItemMapper.selectByExampleWithBLOBs(example);
        if (list != null && list.size() > 0) {
            map.put("itemParamItem", list.get(0).getParamData());
        }
        return map;
    }


    public Integer deleteItemById(Long itemId) {
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        tbItem.setStatus((byte)0);
        Integer i = tbItemMapper.updateByPrimaryKey(tbItem);
        return i;
    }


    public Integer updateTbItem(TbItem tbItem, String desc, String itemParams) {
        TbItem tbItem1 = tbItemMapper.selectByPrimaryKey(tbItem.getId());
        if(tbItem.getSellPoint()!=null){
            tbItem1.setSellPoint(tbItem.getSellPoint());
        }
        if(tbItem.getPrice()!=null){
            tbItem1.setPrice(tbItem.getPrice());
        }
        if(tbItem.getNum()!=null){
            tbItem1.setNum(tbItem.getNum());
        }
        if(tbItem.getImage()!=null){
            tbItem1.setImage(tbItem.getImage());
        }
        if(tbItem.getCid()!=null){
            tbItem1.setCid(tbItem.getCid());
        }
        if(tbItem.getTitle()!=null){
            tbItem1.setTitle(tbItem.getTitle());
        }
        Date date = new Date();
        tbItem1.setUpdated(date);
        int tbItemNum = tbItemMapper.updateByPrimaryKey(tbItem1);

        tbItemDescMapper.deleteByPrimaryKey(tbItem.getId());
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setCreated(date);
        tbItemDesc.setUpdated(date);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setItemId(tbItem.getId());
        int tbItemDescNum = tbItemDescMapper.insertSelective(tbItemDesc);

        TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = tbItemParamItemExample.createCriteria();
        criteria.andItemIdEqualTo(tbItem.getId());
        tbItemParamItemMapper.deleteByExample(tbItemParamItemExample);
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setUpdated(date);
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setItemId(tbItem.getId());
        int tbItemParamItemNum = tbItemParamItemMapper.insert(tbItemParamItem);

        return tbItemNum+tbItemDescNum+tbItemParamItemNum;
    }
}
