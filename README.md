# Nosso sim — site de casamento

Site responsivo com as duas fotografias da pasta `images`, apresentação do casal, espaço para os detalhes do evento e lista de presentes com pratos e copos.

## Tecnologias

Mesma base do projeto em `Documents/Tcc`: Angular 20.3, TypeScript 5.9, Java 21, Spring Boot 3.5.11, Spring Data JPA, Spring Security, Flyway e PostgreSQL. Para desenvolvimento, H2 persiste os dados em `backend/data`.

## Executar no Windows

Abra dois terminais nesta pasta:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/start-backend.ps1
```

```powershell
powershell -ExecutionPolicy Bypass -File scripts/start-frontend.ps1
```

Acesse **http://127.0.0.1:4201**. API: `http://127.0.0.1:8081`.

Os scripts usam Java, Maven e Node do PATH ou as ferramentas portáteis em `.tools`. Nesta máquina também reconhecem as ferramentas existentes em `Documents/Tcc/.tools`. Em outra máquina, instale Java 21, Maven 3.9+ e Node 22.12+; execute `npm ci` em `frontend` antes de iniciar.

## Presentes

O convidado escolhe um item e abre o Pix com valor preenchido: copos por R$ 82,00 e pratos por R$ 110,00. O servidor obtém o preço do banco e gera um BR Code estático; o navegador desenha o QR Code localmente, sem serviço externo. Há também botão para copiar o código e campo selecionável caso a área de transferência não esteja disponível.

**Gerar o Pix não confirma pagamento.** A transferência é um presente em dinheiro para o casal, não uma compra na Amazon. Não há integração bancária, webhook ou confirmação automática. O convidado deve enviar o comprovante aos noivos, que conferem o recebimento no banco. O item não é reservado ao abrir/copiar o Pix e pode receber mais de uma contribuição. As reservas antigas são preservadas; a API de reserva continua disponível para compatibilidade.

Recebedor: Lucas Costa Teotônio, Esperança-PB. Chave: `lucascostateotonio@hotmail.com`. Configuração no backend por `PIX_KEY`, `PIX_NAME` e `PIX_CITY`, com os valores informados como padrão. Nome e cidade são normalizados sem acentos para o BR Code. O banco do pagador consulta o nome associado à chave; o site não verifica sua titularidade. A referência `PRESENTE{id}` identifica o item, não uma cobrança individual nem o pagador.

Implementação baseada no [Manual de Padrões para Iniciação do Pix do Banco Central, versão 2.10.0](https://www.bcb.gov.br/content/estabilidadefinanceira/pix/Regulamento_Pix/II_ManualdePadroesparaIniciacaodoPix.pdf), incluindo CRC16-CCITT. O teste unitário compara o payload com o exemplo publicado pelo BCB.

Pratos e copos são inseridos uma única vez pela migração inicial. Os pratos e copos usam fotos dos anúncios Amazon B0B19274YZ e B0DJ6DRH9J, com descrições resumidas e links para consultar os produtos. As imagens estão em `images/`. Itens sem foto mantêm a ilustração de apoio em CSS.

### Cadastrar novos itens

O cadastro está disponível pela API administrativa, sem painel visual nesta versão. Antes de iniciar o backend, defina `ADMIN_PASSWORD` no terminal. Sem essa variável, nenhum usuário administrador é criado. Escolha sua própria senha.

No PowerShell, com o backend em execução, solicite credenciais (usuário `admin`) e obtenha o token CSRF:

```powershell
$credential = Get-Credential -UserName admin
$csrf = Invoke-RestMethod http://127.0.0.1:8081/api/csrf -Credential $credential -SessionVariable weddingSession
$headers = @{}
$headers[$csrf.headerName] = $csrf.token
$gift = @{ name = 'Jogo de talheres'; description = 'Para os nossos almoços em família.'; category = 'Mesa posta'; price = 95.00 } | ConvertTo-Json
Invoke-RestMethod http://127.0.0.1:8081/api/gifts -Method Post -Credential $credential -WebSession $weddingSession -Headers $headers -ContentType 'application/json; charset=utf-8' -Body ([System.Text.Encoding]::UTF8.GetBytes($gift))
```

Atualize a página para ver o novo item. Nunca inclua a senha no código ou no Git.

Para alterar um preço, envie `PATCH /api/gifts/{id}/price` com JSON `{"price":82.00}`, credenciais administrativas e o mesmo token CSRF. São aceitos somente valores positivos com até duas casas decimais. Itens sem preço exibem “Valor em breve” e não geram Pix. `GET /api/gifts/{id}/pix` retorna o valor e o payload sem cache; parâmetros enviados pelo visitante não alteram o preço.

## Personalizar

- Textos, nomes, data e local: `frontend/src/app.html`. Os detalhes do evento estão identificados como “em breve”; nenhuma data ou local foi inventado.
- Cores, tipografia e layout: `frontend/src/styles.css`.
- Fotografias: `images/`, referenciadas diretamente pelo Angular.
- Google Fonts é usado para tipografia, com fontes locais de fallback se a conexão não estiver disponível.

## PostgreSQL

Crie o banco e configure `DB_URL`, `DB_USER` e `DB_PASSWORD` no processo. Execute `scripts/start-backend.ps1 -Postgres`. Flyway prepara o esquema automaticamente.

## Verificar

```powershell
powershell -ExecutionPolicy Bypass -File scripts/check.ps1
```

O comando compila Angular e executa testes de reserva, conflito, validação, privacidade e cadastro protegido. Maven usa o cache local em modo offline; em uma instalação nova execute os testes com Maven online primeiro.

## Hospedagem

Projeto local, sem publicação. O backend Java exige hospedagem compatível com JVM e banco persistente. Ao publicar, sirva frontend e `/api` na mesma origem com HTTPS, configure as credenciais do banco e do administrador, backups e proteção contra abuso das reservas. O runtime Workers do Sites não executa este backend Java.
