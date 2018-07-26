package com.merge.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.merge.config.Page;
import com.merge.dao.AgreementMapper;
import com.merge.domain.AgreementBean;

@Service
public class AgreementService {

    @Resource
    private AgreementMapper agreementMapper;

    public int getCountAgreement() {
        return agreementMapper.getCountAgreement();
    }

    public List<AgreementBean> getAgreementList(Page<AgreementBean> page) {
        return agreementMapper.getAgreementList(page);
    }

    public AgreementBean getAgreementById(int id) {
        return agreementMapper.getAgreementById(id);
    }

    public int editAgreement(AgreementBean agreement) {
        return agreementMapper.editAgreement(agreement);
    }

    public int addAgreement(AgreementBean agreement) {
        return agreementMapper.addAgreement(agreement);
    }

    public void delAgreement(String ids) {
        agreementMapper.delAgreement(ids);
    }

    public List<AgreementBean> getAllAgreement() {
        return agreementMapper.getAllAgreement();
    }
}
