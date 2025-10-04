package backend.Backend.Controllers;
import backend.Backend.DTOS.ProposalDTO;
import backend.Backend.Repositories.ProductsRepository;
import backend.Backend.Repositories.ProposalProductRepository;
import backend.Backend.Repositories.ProposalRepository;
import backend.Backend.Services.ProposalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@AllArgsConstructor
@RestController
@RequestMapping("/api/proposals")
public class ProposalController {
    private ProposalService proposalService;
    private ProposalRepository proposalRepository;
    private ProductsRepository productsRepository;
    private ProposalProductRepository proposalProductRepository;

    @GetMapping("/findAll")
    public ResponseEntity<List<ProposalDTO>> getAllProposal(){
        List<ProposalDTO> allProposals= proposalService.getAllProposals();
        return ResponseEntity.ok(allProposals);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProposalDTO createProposal(
            @RequestPart("proposal") String proposalJson,
            @RequestPart("file") MultipartFile file) throws IOException {
        return proposalService.createProposal(proposalJson, file);
    }

    @GetMapping("/{proposalId}")
    public ProposalDTO getProposalById(@PathVariable Long proposalId) {
        return proposalService.getProposalById(proposalId);
    }

    @PutMapping(value = "/{proposalId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProposalDTO updateProposal(
            @PathVariable Long proposalId,
            @RequestPart("proposal") String proposalJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        return proposalService.updateProposal(proposalId, proposalJson, file);
    }
    @RequestMapping(value = "/delete/{proposalId}", method = DELETE)
    public ResponseEntity<Void> deleteProposal(@PathVariable Long proposalId) {
        proposalService.deleteProposal(proposalId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{proposalId}")
    public ResponseEntity<byte[]> downloadProposalFile(@PathVariable Long proposalId) {
        byte[] fileContent = proposalService.getProposalFile(proposalId);
        String fileName = proposalService.getProposalFileName(proposalId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Ensure proper filename encoding for different browsers
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            fileName = "proposal.pdf"; // fallback name
        }

        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileName)
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
    @GetMapping("/find/{proposalCode}")
    public ProposalDTO getProposalByCode(@PathVariable String proposalCode) {
        return proposalService.getProposalByCode(proposalCode);
    }

    @GetMapping("/generateNextProposalCode")
    public String generateNextProposalCode(){
        return proposalService.generateNextProposalCode();
    }
    @GetMapping("/findAllByClientId/{clientId}")
    public ResponseEntity<List<ProposalDTO>> getProposalsByClientId(@PathVariable Long clientId) {
        List<ProposalDTO> proposals = proposalService.getProposalsByClientId(clientId);
        return ResponseEntity.ok(proposals);
    }


}
