package com.marriage;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
@RestController @RequestMapping("/api/gifts")
public class GiftController {
 private final GiftRepository repo;
 public GiftController(GiftRepository repo){this.repo=repo;}
 public record View(Long id,String name,String description,String category,boolean reserved,String imageUrl,String productUrl,java.math.BigDecimal price){static View of(Gift g){return new View(g.id,g.name,g.description,g.category,g.reserved,g.imageUrl,g.productUrl,g.price);}}
 public record Reservation(@NotBlank @Size(min=2,max=100) String guest,@Size(max=500) String message){}
 public record NewGift(@NotBlank @Size(max=120) String name,@NotBlank @Size(max=500) String description,@NotBlank @Size(max=80) String category,@DecimalMin("0.01") @Digits(integer=10,fraction=2) java.math.BigDecimal price){}
 @GetMapping public List<View> list(){return repo.findAll(org.springframework.data.domain.Sort.by("id")).stream().map(View::of).toList();}
 public record PriceUpdate(@NotNull @DecimalMin("0.01") @Digits(integer=10,fraction=2) java.math.BigDecimal price){}
 @PatchMapping("/{id}/price") @Transactional public View price(@PathVariable Long id,@Valid @RequestBody PriceUpdate body){Gift g=repo.lockById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));g.price=body.price();return View.of(repo.save(g));}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public View create(@Valid @RequestBody NewGift body){Gift g=new Gift();g.name=body.name();g.description=body.description();g.category=body.category();g.price=body.price();return View.of(repo.save(g));}
 @PostMapping("/{id}/reserve") @Transactional public View reserve(@PathVariable Long id,@Valid @RequestBody Reservation body){if(body.guest().strip().length()<2)throw new ResponseStatusException(HttpStatus.BAD_REQUEST);Gift g=repo.lockById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(g.reserved)throw new ResponseStatusException(HttpStatus.CONFLICT);g.reserved=true;g.guestName=body.guest().strip();g.message=body.message();return View.of(repo.save(g));}
}
