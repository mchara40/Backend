package backend.Backend.Repositories;


import backend.Backend.Entities.Receipts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptsRepository  extends JpaRepository<Receipts,Long> {
    Optional<Receipts> findTopByOrderByReceiptCodeDesc();
    List<Receipts> findByInvoiceId(Long invoiceId);
}
