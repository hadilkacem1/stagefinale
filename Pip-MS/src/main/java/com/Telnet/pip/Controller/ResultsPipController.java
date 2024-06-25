package com.Telnet.pip.Controller;

import com.Telnet.pip.model.Pip;
import com.Telnet.pip.model.ResultsPip;
import com.Telnet.pip.repository.PipRepository;
import com.Telnet.pip.repository.ResultsPipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/resultspip")
@PreAuthorize("hasAnyAuthority('ROLE_CHEFDEPROJET', 'ROLE_RESPONSABLEQUALITE')")
public class ResultsPipController {

    private static final Logger logger = LoggerFactory.getLogger(ResultsPipController.class);

    @Autowired
    private ResultsPipRepository resultspipRepository;

    @Autowired
    private PipRepository pipRepository;

    @GetMapping("/list")
    public List<ResultsPip> getResultsPipList() {
        return resultspipRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultsPip> getResultsPipById(@PathVariable(value = "id") Long resultspipId)
            throws ResourceNotFoundException {
        ResultsPip resultspip = resultspipRepository.findById(resultspipId)
                .orElseThrow(() -> new ResourceNotFoundException("ResultsPip not found ::" + resultspipId));
        return ResponseEntity.ok().body(resultspip);
    }

    @PostMapping("/add")
    public ResultsPip createResultsPip(@Valid @RequestBody ResultsPip resultspip) {
        return resultspipRepository.save(resultspip);
    }

    // ADD RESULTS PIP WITH PIP ID
    @PostMapping("add/{pip_id}")
    public ResultsPip createResultsPip(@PathVariable(value = "pip_id") Long pip_id,
                                       @Valid @RequestBody ResultsPip resultspip)
            throws ResourceNotFoundException {
        return pipRepository.findById(pip_id).map(pip -> {
            resultspip.setPip(pip);
            return resultspipRepository.save(resultspip);
        }).orElseThrow(() -> new ResourceNotFoundException("PIP not found"));
    }

    @PutMapping("/{id}/{pip_id}")
    public ResponseEntity<ResultsPip> updateResultsPip(
            @PathVariable(value = "id") Long resultspipId, @PathVariable(value = "pip_id") Long pip_id,
            @Valid @RequestBody ResultsPip resultspipDetails) throws ResourceNotFoundException {
        logger.info("{}", pipRepository.findById(pip_id).get().getName());

        Optional<Pip> pip = pipRepository.findById(pip_id);

        ResultsPip resultspip = resultspipRepository.findById(resultspipId)
                .orElseThrow(() -> new ResourceNotFoundException("Results PIP not found :: " + resultspipId));
        resultspip.setExpectation(resultspipDetails.getExpectation());
        resultspip.setRisk(resultspipDetails.getRisk());
        resultspip.setExistantMonitoring(resultspipDetails.getExistantMonitoring());
        resultspip.setSetupMonitoring(resultspipDetails.getSetupMonitoring());
        resultspip.setProcessus(resultspipDetails.getProcessus());
        resultspip.setPip(pip.get());

        final ResultsPip updatedResultsPip = resultspipRepository.save(resultspip);
        return ResponseEntity.ok(updatedResultsPip);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteResultsPip(
            @PathVariable(value = "id") Long resultspipId) throws ResourceNotFoundException {
        ResultsPip instructor = resultspipRepository.findById(resultspipId)
                .orElseThrow(() -> new ResourceNotFoundException("Results PIP not found :: " + resultspipId));

        resultspipRepository.delete(instructor);
        Map<String, Boolean> response = new HashMap<>();
        response.put("Results PIP successefully deleted", Boolean.TRUE);
        return response;
    }

}
