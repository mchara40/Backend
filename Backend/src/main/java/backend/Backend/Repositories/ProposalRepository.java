package backend.Backend.Repositories;


import backend.Backend.Entities.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal,Long> {
    Optional<Proposal> findTopByOrderByCodeDesc();
    Proposal findByCode(String code);
    List<Proposal> findAllByClientId(Long clientId);
}
