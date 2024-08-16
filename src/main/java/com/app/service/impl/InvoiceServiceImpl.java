package com.app.service.impl;

import com.app.model.Invoice;
import com.app.model.InvoiceDetail;
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
import java.util.List;
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

    private Mono<Invoice> poblarCliente(Invoice invoice){
        return clientRepo.findById(invoice.getClient().getId())
                .map(client -> {
                    invoice.setClient(client);
                    return invoice;
                });
    }

    private Mono<Invoice> poblarItems(Invoice invoice){
        List<Mono<InvoiceDetail>> list = invoice.getItems().stream()// Stream<InvoiceDetail>
                .map(detail -> dishRepo.findById(detail.getDish().getId())//Mono<Dish>
                        .map(dish -> {
                            detail.setDish(dish);
                            return detail;
                        })
                ).toList();

        //Mono when espera a que todos de la lista se procese para ejecutar then
        return Mono.when(list).then(Mono.just(invoice));
    }

    private byte[] generarPDF(Invoice invoice){
            try ( InputStream stream = getClass().getResourceAsStream("/facturas.jrxml")){
                Map<String,Object> params = new HashMap<>();
                params.put("txt_client",invoice.getClient().getFirstName()+" "+invoice.getClient().getLastName());

                JasperReport jasper = JasperCompileManager.compileReport(stream);
                JasperPrint print = JasperFillManager.fillReport(jasper,params,new JRBeanCollectionDataSource(invoice.getItems()));
                return  JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                return new byte[0];
            }
    }

    @Override
    public Mono<byte[]> generateReport(String idInvoice) {
        return invoiceRepo.findById(idInvoice)
                .flatMap(this::poblarCliente)
                .flatMap(this::poblarItems)
                .map(this::generarPDF)
                .onErrorResume(e->Mono.empty());
    }

   /* @Override
    public Mono<byte[]> generateReport(String idInvoice) {
        return invoiceRepo.findById(idInvoice)
                //Obteniendo Client
                .flatMap(invoice -> Mono.just(invoice)
                            .zipWith(clientRepo.findById(invoice.getClient().getId()), (inv, client)-> {
                                inv.setClient(client);
                                return inv;
                            })
                )
                //Obteniendo cada Dish
                .flatMap(invoice -> {
                    return Flux.fromIterable(invoice.getItems())
                            .flatMap(detail ->{
                                return dishRepo.findById(detail.getDish().getId())
                                        .map( dish -> {
                                            detail.setDish(dish);
                                            return detail;
                                        });
                            }).collectList()
                            .flatMap(list ->{
                                invoice.setItems(list);
                                return Mono.just(invoice);
                            });
                })
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
    }*/
}
