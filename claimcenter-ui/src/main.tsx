import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import { ToastContainer, Bounce } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Home from "./components/Home.tsx";
import Login, { loginAction } from "./components/Login.tsx";
import ErrorPage from "./components/ErrorPage.tsx";
import {
  createBrowserRouter,
  RouterProvider,
  createRoutesFromElements,
  Route,
} from "react-router-dom";
import Register, { RegisterAction } from "./components/Register.tsx";
import { AuthProvider } from "./store/auth-context.tsx";
import Claims from "./components/claims.tsx";

const routeDefinitions = createRoutesFromElements(
  <Route path="/" element={<App />} errorElement={<ErrorPage />}>
    <Route index element={<Home />} />
    <Route path="home" element={<Home />} />
    <Route path="login" element={<Login />} action={loginAction} />
    <Route path="register" element={<Register />} action={RegisterAction} />
    <Route path="claims" element={<Claims />} />
  </Route>
);

const appRouter = createBrowserRouter(routeDefinitions);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <AuthProvider>
      <RouterProvider router={appRouter} />
      <ToastContainer
        position="top-center"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        draggable
        pauseOnHover
        theme="light"
        transition={Bounce}
      />
    </AuthProvider>
  </StrictMode>,
);
