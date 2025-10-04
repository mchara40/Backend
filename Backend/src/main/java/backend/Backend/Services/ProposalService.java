package backend.Backend.Services;

import backend.Backend.DTOS.ProposalDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProposalService {
    List<ProposalDTO> getAllProposals();
    ProposalDTO createProposal(String proposalJson, MultipartFile file) throws IOException;
    ProposalDTO getProposalById(Long Id);
    List<ProposalDTO> getProposalsByClientId(Long clientId);
    ProposalDTO updateProposal(Long proposalId, String proposalJson, MultipartFile file) throws IOException;
    void deleteProposal(Long proposalId);
    byte[] getProposalFile(Long proposalId);
    String getProposalFileName(Long proposalId);
    ProposalDTO getProposalByCode(String code);
    String generateNextProposalCode();

}