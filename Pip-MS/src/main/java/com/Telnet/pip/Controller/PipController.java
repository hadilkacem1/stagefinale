package com.Telnet.pip.Controller;


import com.Telnet.pip.model.Category;
import com.Telnet.pip.model.Pip;
import com.Telnet.pip.model.ResultsPip;
import com.Telnet.pip.repository.CategoryRepository;
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
@RequestMapping("/api/pip")
@PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
public class PipController {

    private static final Logger logger = LoggerFactory.getLogger(PipController.class);

    @Autowired
    private PipRepository pipRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired

    private ResultsPipRepository resultsPipRepository;

    @GetMapping("/listpips")
    public String getListOfPips() {
        // Données JSON statiques
        String jsonData = "{\"pips\": [{\"id\": 1, \"name\": \"Pip1\"}, {\"id\": 2, \"name\": \"Pip2\"}]}";
        return jsonData;
    }
    @GetMapping("/list")
    public List<Pip> getPipList() {
        return pipRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pip> getPipById(@PathVariable(value = "id") Long pipId) throws ResourceNotFoundException {
        Pip pip = pipRepository.findById(pipId)
                .orElseThrow(() -> new ResourceNotFoundException("Pip not found ::" + pipId));
        return ResponseEntity.ok().body(pip);
    }

    // @PostMapping("/add")
    // public Pip createPip( @Valid @RequestBody Pip pip){
    // return pipRepository.save(pip);
    // }

    @PostMapping("add/category/{category_id}")
    public Pip createPip(@PathVariable(value = "category_id") Long category_id, @Valid @RequestBody Pip pip)
            throws ResourceNotFoundException {
        return categoryRepository.findById(category_id).map(category -> {
            pip.setCategory(category);
            return pipRepository.save(pip);
        }).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
    @GetMapping("/byCategory/{categoryId}")
    public List<Pip> getPipsByCategory(@PathVariable Long categoryId) {
        return pipRepository.findByCategoryId(categoryId);
    }
    @PutMapping("/{id}/{category_id}")
    public ResponseEntity<Pip> updatePip(
            @PathVariable(value = "id") Long pipId,
            @PathVariable(value = "category_id") Long category_id,
            @Valid @RequestBody Pip pipDetails) throws ResourceNotFoundException {

        logger.info("{}", categoryRepository.findById(category_id).get().getName());

        Optional<Category> category = categoryRepository.findById(category_id);

        Pip pip = pipRepository.findById(pipId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found :: " + pipId));
        pip.setName(pipDetails.getName());
        pip.setType(pipDetails.getType());
        pip.setInteraction(pipDetails.getInteraction());
        pip.setCategory(category.get());

        final Pip updatedPip = pipRepository.save(pip);
        return ResponseEntity.ok(updatedPip);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deletePip(
            @PathVariable(value = "id") Long pipId) throws ResourceNotFoundException {
        Pip pip = pipRepository.findById(pipId)
                .orElseThrow(() -> new ResourceNotFoundException("Pip not found :: " + pipId));

        // Suppression des enregistrements associés dans resultpips
        List<ResultsPip> resultPips = resultsPipRepository.findByPipId(pipId);
        for (ResultsPip resultPip : resultPips) {
            resultsPipRepository.delete(resultPip);
        }

        pipRepository.delete(pip);

        Map<String, Boolean> response = new HashMap<>();
        response.put("Pip successfully deleted", Boolean.TRUE);
        return response;
    }
}