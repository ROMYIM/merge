package com.merge.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.merge.config.Page;
import com.merge.domain.AgreementAccountBean;

@Mapper
public interface AgreementAccountMapper {

    List<AgreementAccountBean> getAccountList(Page<AgreementAccountBean> page);

    int getCountAccount();

    int addAccount(AgreementAccountBean account);

    void delAccount(@Param("ids")String ids);

    List<AgreementAccountBean> getAccountLimitList(@Param("offset")int offset, @Param("limit")int limit);

    void setStatusAndError(AgreementAccountBean account);

    List<AgreementAccountBean> getAccountStatus(Page<AgreementAccountBean> page);

    int getCountAccountStatus(@Param("status")int status, @Param("type")String type);

    List<AgreementAccountBean> selectAllAccountByStatus();

    List<String> selectAgreementTypeFromAccount();

    List<AgreementAccountBean> selectAccountsByType(@Param("type")String type);

}
