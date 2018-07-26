package com.merge.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.merge.config.Page;
import com.merge.dao.AgreementAccountMapper;
import com.merge.domain.AgreementAccountBean;

@Service
public class AgreementAccountService {
    
    @Resource
    private AgreementAccountMapper agreementAccountMapper;

    public List<AgreementAccountBean> getAccountList(Page<AgreementAccountBean> page) {
        return agreementAccountMapper.getAccountList(page);
    }

    public int getCountAccount() {
        return agreementAccountMapper.getCountAccount();
    }

    public int addAccount(AgreementAccountBean account) {
        return agreementAccountMapper.addAccount(account);
    }

    public void delAccount(String ids) {
        agreementAccountMapper.delAccount(ids);
    }

    public List<AgreementAccountBean> getAccountLimitList(int offset, int limit) {
        return agreementAccountMapper.getAccountLimitList(offset, limit);
    }

    public void setStatusAndError(AgreementAccountBean account) {
        agreementAccountMapper.setStatusAndError(account);
    }

    public List<AgreementAccountBean> getAccountStatus(Page<AgreementAccountBean> page) {
        return agreementAccountMapper.getAccountStatus(page);
    }

    public int getCountAccountStatus(int status, String type) {
        return agreementAccountMapper.getCountAccountStatus(status, type);
    }


}
