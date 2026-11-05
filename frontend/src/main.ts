import { Component, OnInit, provideZonelessChangeDetection, signal } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { CurrencyPipe, registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';
import * as QRCode from 'qrcode';
interface Gift { id:number; name:string; description:string; category:string; reserved:boolean; price:number|null; imageUrl?:string; productUrl?:string }
interface Pix { payload:string; amount:number; key:string; recipient:string; city:string; reference:string }
@Component({selector:'app-root',standalone:true,imports:[CurrencyPipe],templateUrl:'./app.html'})
class App implements OnInit {
  gifts=signal<Gift[]>([]); loading=signal(true); error=signal(''); filter='Todos';
  selected:Gift|null=null; payment=signal<Pix|null>(null); qr=signal(''); busy=signal(false); formError=signal(''); copyStatus=signal('');
  private request?:AbortController;
  get filtered(){return this.gifts().filter(g=>this.filter==='Todos'||(this.filter==='Disponíveis'?!g.reserved:g.category===this.filter));}
  async ngOnInit(){await this.load();}
  async load(){this.loading.set(true);this.error.set('');try{const r=await fetch('/api/gifts');if(!r.ok)throw Error();this.gifts.set(await r.json());}catch{this.error.set('Não foi possível carregar os presentes. Tente novamente em instantes.');}finally{this.loading.set(false);}}
  open(g:Gift){this.selected=g;this.payment.set(null);this.qr.set('');this.formError.set('');this.copyStatus.set('');document.querySelector<HTMLDialogElement>('dialog')?.showModal();void this.generatePix();}
  close(){this.request?.abort();document.querySelector<HTMLDialogElement>('dialog')?.close();this.selected=null;this.payment.set(null);this.qr.set('');this.busy.set(false);}
  async generatePix(){
    if(!this.selected)return;
    this.request?.abort();const request=new AbortController();this.request=request;
    this.busy.set(true);this.formError.set('');this.payment.set(null);this.qr.set('');this.copyStatus.set('');
    try{
      const response=await fetch(`/api/gifts/${this.selected.id}/pix`,{signal:request.signal,cache:'no-store'});
      if(!response.ok)throw Error(response.status===409?'Este presente está indisponível ou ainda não tem preço definido.':response.status===503?'O Pix está temporariamente indisponível. Tente novamente mais tarde.':'Não foi possível gerar o Pix. Tente novamente.');
      const payment:Pix=await response.json();if(request.signal.aborted)return;this.payment.set(payment);
      try {const qr=await QRCode.toDataURL(payment.payload,{width:320,margin:4,errorCorrectionLevel:'M'});if(!request.signal.aborted)this.qr.set(qr);}
      catch {if(!request.signal.aborted)this.formError.set('Não foi possível desenhar o QR Code. Você ainda pode usar o Pix copia e cola abaixo.');}
    }catch(error){if(!request.signal.aborted)this.formError.set((error as Error).message);}
    finally{if(!request.signal.aborted)this.busy.set(false);}
  }
  async copyPix(){const payment=this.payment();if(!payment)return;try{await navigator.clipboard.writeText(payment.payload);this.copyStatus.set('Código Pix copiado! Cole no aplicativo do seu banco.');}catch{const field=document.querySelector<HTMLTextAreaElement>('#pix-code');field?.focus();field?.select();this.copyStatus.set('Selecione e copie o código abaixo manualmente.');}}
}
registerLocaleData(localePt);
bootstrapApplication(App, { providers: [provideZonelessChangeDetection()] }).catch(console.error);
