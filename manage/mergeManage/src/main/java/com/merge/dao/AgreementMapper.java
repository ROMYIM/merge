package com.merge.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.merge.config.Page;
import com.merge.domain.AgreementBean;

@Mapper
public interface AgreementMapper {

    int getCountAgreement();

    List<AgreementBean> getAgreementList(Page<AgreementBean> page);

    AgreementBean getAgreementById(@Param("id")int id);

    int editAgreement(AgreementBean agreement);

    int addAgreement(AgreementBean agreement);

    void delAgreement(@Param("ids")String ids);

    List<AgreementBean> getAllAgreement();

}
