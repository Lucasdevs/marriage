import { ChangeDetectorRef, Component, OnInit, inject, provideZonelessChangeDetection, signal } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
interface Gift { id:number; name:string; description:string; category:string; reserved:boolean; imageUrl?:string; productUrl?:string }
@Component({selector:'app-root',standalone:true,imports:[FormsModule],templateUrl:'./app.html'})
class App implements OnInit {
  private readonly changeDetector = inject(ChangeDetectorRef);
  gifts=signal<Gift[]>([]); loading=signal(true); error=signal(''); filter='Todos'; selected:Gift|null=null; guest=''; message=''; busy=false; success=''; formError='';
  get filtered(){return this.gifts().filter(g=>this.filter==='Todos'||(this.filter==='Disponíveis'?!g.reserved:g.category===this.filter));}
  async ngOnInit(){await this.load();}
  async load(){this.loading.set(true);this.error.set('');try{const r=await fetch('/api/gifts');if(!r.ok)throw Error();this.gifts.set(await r.json());}catch{this.error.set('Não foi possível carregar os presentes. Tente novamente em instantes.');}finally{this.loading.set(false);}}
  open(g:Gift){this.selected=g;this.formError='';this.guest='';this.message='';setTimeout(()=>document.querySelector<HTMLDialogElement>('dialog')?.showModal());}
  close(){if(this.busy)return;document.querySelector<HTMLDialogElement>('dialog')?.close();this.selected=null;}
  async reserve(){if(!this.selected||this.busy)return;this.busy=true;this.formError='';try{const r=await fetch('/api/gifts/'+this.selected.id+'/reserve',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({guest:this.guest.trim(),message:this.message.trim()})});if(!r.ok)throw Error(r.status===409?'Este presente acabou de ser reservado. Escolha outro com carinho.':'Não conseguimos reservar. Confira seu nome e tente novamente.');this.success='Seu presente foi reservado! Obrigado por fazer parte da nossa história. Combine a entrega diretamente com os noivos.';this.busy=false;this.close();await this.load();}catch(e){this.formError=(e as Error).message;}finally{this.busy=false;this.changeDetector.markForCheck();}}
}
bootstrapApplication(App, { providers: [provideZonelessChangeDetection()] }).catch(console.error);
