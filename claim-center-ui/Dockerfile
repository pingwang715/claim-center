# Build stage
FROM node:24-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Runtime stage
FROM node:24-alpine

WORKDIR /app

# Install a simple HTTP server to serve static files
RUN npm install -g http-server

# Copy built files from builder
COPY --from=builder /app/dist dist

# Expose port
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:3000/ || exit 1

# Serve the application
CMD ["http-server", "dist", "-p", "3000"]
