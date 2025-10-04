package backend.Backend.Repositories;

import backend.Backend.Entities.Proposal;
import backend.Backend.Entities.ProposalProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProposalProductRepository extends JpaRepository<ProposalProduct, Long> {
    List<ProposalProduct> findAllByProposal(Proposal proposal);
    List<ProposalProduct> findByProposalId(Long proposalId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM proposal_product WHERE proposal_id = :proposalId", nativeQuery = true)
    void deleteAllByProposalById(@Param("proposalId") Long proposalId);

}

