package com.app.service.impl;

import com.app.model.Invoice;
import com.app.repo.IClientRepo;
import com.app.repo.IDishRepo;
import com.app.repo.IInvoiceRepo;
import com.app.repo.IGenericRepo;
import com.app.service.IInvoiceService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl extends CRUDImpl<Invoice,String> implements IInvoiceService {

    public final IInvoiceRepo invoiceRepo;
    public final IClientRepo clientRepo;
    public final IDishRepo dishRepo;

    @Override
    protected IGenericRepo<Invoice, String> getRepo() {
        return invoiceRepo;
    }

    @Override
    public Mono<byte[]> generateReport(String idInvoice) {
        return invoiceRepo.findById(idInvoice)
                .map(inv -> {
                    try{
                        Map<String,Object> params = new HashMap<>();
                        params.put("txt_client",inv.getClient().getFirstName()+" "+inv.getClient().getLastName());

                        InputStream jrxml = getClass().getResourceAsStream("/facturas.jrxml");
                        JasperReport jasper = JasperCompileManager.compileReport(jrxml);
                        JasperPrint print = JasperFillManager.fillReport(jasper,params,new JRBeanCollectionDataSource(inv.getItems()));
                        return  JasperExportManager.exportReportToPdf(print);
                    } catch (JRException e) {
                        return new byte[0];
                    }
                });
    }
}
