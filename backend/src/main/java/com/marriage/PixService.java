package com.marriage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PixService {
 private final String key, name, city;
 public PixService(@Value("${pix.key}") String key, @Value("${pix.name:}") String name, @Value("${pix.city:}") String city) {
  this.key=key.strip(); this.name=normalize(name,25); this.city=normalize(city,15);
 }
 public record Payment(String payload, BigDecimal amount, String key, String recipient, String city, String reference) {}
 public Payment payment(Gift gift) {
  if (gift.reserved) throw new ResponseStatusException(HttpStatus.CONFLICT,"Presente indisponível.");
  if (gift.price==null || gift.price.signum()<=0) throw new ResponseStatusException(HttpStatus.CONFLICT,"Preço ainda não definido.");
  if (key.isBlank() || name.isBlank() || city.isBlank()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Dados do Pix ainda não configurados.");
  String reference="PRESENTE"+gift.id;
  return new Payment(encode(key,name,city,gift.price,reference),gift.price,key,name,city,reference);
 }
 static String normalize(String text,int max) {
  String value=Normalizer.normalize(text,Normalizer.Form.NFD).replaceAll("\\p{M}","").toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9 ]","").strip().replaceAll(" +"," ");
  return value.substring(0,Math.min(max,value.length()));
 }
 static String field(String id,String value) {
  int length=value.getBytes(StandardCharsets.UTF_8).length;
  if(length>99) throw new IllegalArgumentException("Campo Pix excede 99 bytes");
  return id+String.format(Locale.ROOT,"%02d",length)+value;
 }
 static String encode(String key,String name,String city,BigDecimal amount,String reference) {
  if(amount!=null && (amount.signum()<=0 || amount.compareTo(new BigDecimal("9999999999.99"))>0)) throw new IllegalArgumentException("Valor inválido");
  if(!reference.matches("[a-zA-Z0-9]{1,25}|\\*\\*\\*")) throw new IllegalArgumentException("Referência inválida");
  String payload=field("00","01")+field("26",field("00","br.gov.bcb.pix")+field("01",key))+field("52","0000")+field("53","986")
   +(amount==null?"":field("54",amount.setScale(2,RoundingMode.UNNECESSARY).toPlainString()))
   +field("58","BR")+field("59",name)+field("60",city)+field("62",field("05",reference))+"6304";
  return payload+crc(payload);
 }
 static String crc(String payload) {
  int crc=0xFFFF;
  for(byte b:payload.getBytes(StandardCharsets.UTF_8)) {crc^=(b&0xFF)<<8;for(int bit=0;bit<8;bit++)crc=((crc&0x8000)!=0?(crc<<1)^0x1021:crc<<1)&0xFFFF;}
  return String.format(Locale.ROOT,"%04X",crc);
 }
}
