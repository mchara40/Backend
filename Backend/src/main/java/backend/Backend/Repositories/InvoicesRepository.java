package backend.Backend.Repositories;


import backend.Backend.DTOS.InvoicesDTO;
import backend.Backend.Entities.InvoiceStatus;
import backend.Backend.Entities.Invoices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoicesRepository extends JpaRepository<Invoices,Long> {
    Optional<Invoices> findTopByOrderByCodeDesc();
    List<Invoices> findByProposalId(Long proposalId);
    Boolean existsByCode(String code);
    List<Invoices> findAllByClientId(Long clientId);
    List<Invoices> findAllByClientIdAndInvoiceStatusNot(Long clientId, InvoiceStatus invoiceStatus);
    List<Invoices> findAllByInvoiceStatusNot(InvoiceStatus invoiceStatus);
}
