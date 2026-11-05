package com.marriage;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;
@RestController
public class PixController {
 private final GiftRepository gifts; private final PixService pix;
 public PixController(GiftRepository gifts,PixService pix){this.gifts=gifts;this.pix=pix;}
 @GetMapping("/api/gifts/{id}/pix") public ResponseEntity<PixService.Payment> get(@PathVariable Long id){
  Gift gift=gifts.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(pix.payment(gift));
 }
}
