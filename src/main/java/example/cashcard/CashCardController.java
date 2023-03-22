package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        Optional<CashCard> optionalCashCard = cashCardRepository.findById(requestedId);
        return optionalCashCard.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

//    @GetMapping()
//    public ResponseEntity<Iterable<CashCard>> findAll(){
//        return ResponseEntity.ok(cashCardRepository.findAll());
//    }

    @GetMapping()
    public ResponseEntity<Collection<CashCard>> findAll(Pageable pageable){
        Page<CashCard> page = cashCardRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        // previous was getSort()
                        // so if there was not asc or desc specified it will sort by asc
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));

        return ResponseEntity.ok(page.toList());
    }


    @PostMapping
    public ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ubc) {
        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);

        URI locationOfNewCashCard = ubc
                .path("/cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();

        return ResponseEntity.created(locationOfNewCashCard).build();
    }

}
