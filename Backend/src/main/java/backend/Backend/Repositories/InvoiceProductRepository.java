package backend.Backend.Repositories;

import backend.Backend.Entities.InvoiceProduct;
import backend.Backend.Entities.Invoices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {
    List<InvoiceProduct> findAllByInvoices(Invoices invoices);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM invoice_product WHERE invoice_id = :invoiceId", nativeQuery = true)
    void deleteAllByInvoiceById(@Param("invoiceId") Long invoiceId);

    @Query("SELECT ip FROM InvoiceProduct ip WHERE ip.invoiceId = :invoiceId")
    List<InvoiceProduct> findAllByInvoiceId(@Param("invoiceId") Long invoiceId);
}
