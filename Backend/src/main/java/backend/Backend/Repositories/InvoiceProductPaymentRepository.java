package backend.Backend.Repositories;


import backend.Backend.Entities.InvoiceProductPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceProductPaymentRepository extends JpaRepository<InvoiceProductPayment, Long> {
    List<InvoiceProductPayment> findByInvoiceProductId(Long invoiceProductId);
}
