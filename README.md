# SecureWatch AI

AI-powered security monitoring and threat detection platform with React, Spring Boot, MySQL, JWT authentication, Nmap, VirusTotal, and AI-generated remediation guidance.

## Folder Structure

```text
SecureWatch AI/
  backend/              Spring Boot REST API, JWT security, scanners
  frontend/             React + Tailwind dashboard
  database/schema.sql   MySQL schema
  docker-compose.yml    MySQL + API + frontend deployment
```

## Backend Setup

```bash
cd backend
cp .env.example .env
mvn spring-boot:run
```

The API runs on `http://localhost:8080`. Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.

Important environment variables:

```text
DB_URL=jdbc:mysql://localhost:3306/securewatch_ai?createDatabaseIfNotExist=true
DB_USERNAME=root
DB_PASSWORD=password
JWT_SECRET=replace-with-at-least-32-random-characters
AI_API_KEY=optional-openai-or-compatible-key
VIRUSTOTAL_API_KEY=optional-virustotal-key
NMAP_PATH=nmap
ALLOW_ACTIVE_CHECKS=false
```

## Frontend Setup

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

The frontend runs on `http://localhost:5173`.

## Docker Setup

```bash
docker compose up --build
```

Frontend: `http://localhost:8081`  
Backend: `http://localhost:8080`  
MySQL: `localhost:3306`

## API Samples

Register:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin","email":"admin@securewatch.local","password":"Password123!"}'
```

Login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@securewatch.local","password":"Password123!"}'
```

Website scan:

```bash
curl -X POST http://localhost:8080/api/scans/website \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"target":"https://example.com"}'
```

Port scan:

```bash
curl -X POST http://localhost:8080/api/scans/ports \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"target":"scanme.nmap.org"}'
```

Malware hash lookup:

```bash
curl -X POST http://localhost:8080/api/scans/malware \
  -H "Authorization: Bearer TOKEN" \
  -F "file=@sample.bin"
```

## Deployment

Render or Railway backend:

1. Create a MySQL database.
2. Deploy `backend/` as a Docker service or Java service.
3. Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`, `AI_API_KEY`, and `VIRUSTOTAL_API_KEY`.
4. Install or include Nmap if port scanning is enabled.

Vercel frontend:

1. Deploy `frontend/`.
2. Set `VITE_API_URL=https://your-api.example.com/api`.
3. Build command: `npm run build`.
4. Output directory: `dist`.

## Security Notes

Only scan systems you own or have explicit permission to test. Active checks are disabled by default with `ALLOW_ACTIVE_CHECKS=false`. The website scanner uses mostly passive checks unless you opt in.
