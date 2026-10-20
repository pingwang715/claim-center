import { defineConfig } from 'vite'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'
import tailwindcss from "@tailwindcss/vite"

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
    babel({ presets: [reactCompilerPreset()] })
  ],
  build: {
    outDir: "dist",
    sourcemap: false,
    rolldownOptions: {
      output: {
        codeSplitting: {
          groups: [
            {
              name: "vendor",
              test: /node_modules[\\/](react|react-dom|scheduler)[\\/]/,
            },
            {
              name: "redux",
              test: /node_modules[\\/](@reduxjs[\\/]toolkit|react-redux|redux|redux-thunk|reselect|immer)[\\/]/,
            },
            {
              name: "router",
              test: /node_modules[\\/](react-router|react-router-dom)[\\/]/,
            },
            {
              name: "ui",
              test: /node_modules[\\/]@fortawesome[\\/]/,
            },
          ],
        },
      },
    },
  },
  base: "/",
  server: { port: 5173 },
  preview: { port: 5173 },
})
