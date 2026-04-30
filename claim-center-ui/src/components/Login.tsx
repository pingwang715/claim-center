import React, { useEffect } from "react";
import PageTitle from "./PageTitle";
import {
  Link,
  Form,
  useActionData,
  useNavigation,
  useNavigate,
} from "react-router-dom";
import apiClient from "../api/apiClient";
import { toast } from "react-toastify";
import type { ActionFunctionArgs } from "react-router-dom";
import type { AxiosError } from "axios";
import type { User } from "../types";
import { useAuth } from "../store/auth-context";

export default function Login(): React.JSX.Element {
  const actionData = useActionData<loginActionResult>();
  const navigation = useNavigation();
  const isSubmitting: boolean = navigation.state === "submitting";
  const navigate = useNavigate();
  const from = sessionStorage.getItem("redirectPath") || "/home";
  const { loginSuccess } = useAuth();

  useEffect(() => {
    if (actionData?.success) {
      loginSuccess(actionData.jwtToken!, actionData.user!);
      sessionStorage.removeItem("redirectPath");
      setTimeout(() => {
        navigate(from);
      }, 100);
    } else if (actionData?.errors) {
      toast.error(actionData.errors.message || "Login failed.");
    }
  }, [actionData]);

  const lableStyle: string = "block text-lg font-semibold text-primary mb-2";
  const textFieldStyle: string =
    "w-full px-4 py-2 text-base border rounded-md transition border-primary focus:ring focus:ring-dark focus:outline-none text-secondary bg-white placeholder-gray-400";

  return (
    <div className="min-h-[820px] flex items-center justify-center font-primary bg-normalbg">
      <div className="bg-white shadow-md rounded-lg max-w-md w-full px-8 py-6">
        <PageTitle title="Login" />
        <Form method="POST" className="space-y-6">
          <div>
            <label htmlFor="username" className={lableStyle}>
              Username
            </label>
            <input
              type="text"
              id="username"
              name="username"
              placeholder="Your Username"
              required
              className={textFieldStyle}
            />
          </div>

          <div>
            <label htmlFor="password" className={lableStyle}>
              Password
            </label>
            <input
              type="text"
              id="password"
              name="password"
              placeholder="Your Password"
              required
              minLength={4}
              maxLength={20}
              className={textFieldStyle}
            />
          </div>

          <div>
            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full px-6 py-2 text-white text-xl rounded-md transition duration-200 bg-primary hover:bg-dark"
            >
              {isSubmitting ? "Authenticating..." : "Login"}
            </button>
          </div>
        </Form>

        <p className="text-center text-gray-600 mt-4">
          Don't have an account?{" "}
          <Link
            to="/register"
            className="text-primary hover:text-dark transition duration-200"
          >
            Register Here
          </Link>
        </p>
      </div>
    </div>
  );
}

interface LoginResponse {
  message: string;
  user: User;
  jwtToken: string;
}

interface loginActionResult {
  success: boolean;
  message?: string;
  user?: User;
  jwtToken?: string;
  errors?: { message: string };
}

export async function loginAction({
  request,
}: ActionFunctionArgs): Promise<loginActionResult> {
  const data = await request.formData();

  const loginData = {
    username: data.get("username") as string,
    password: data.get("password") as string,
  };

  try {
    const response = await apiClient.post<LoginResponse>(
      "/auth/login",
      loginData,
    );
    const { message, user, jwtToken } = response.data;
    return { success: true, message, user, jwtToken };
  } catch (err) {
    const error = err as AxiosError<{ message: string }>;
    if (error.response?.status === 401) {
      return {
        success: false,
        errors: { message: "Invalid username or password" },
      };
    }
    throw new Response(
      error.response?.data?.message ||
        error.message ||
        "Failed to login. Please try again.",
      { status: error.response?.status || 500 },
    );
  }
}
