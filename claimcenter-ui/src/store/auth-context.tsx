import { createContext, useEffect, useContext, useReducer } from "react";
import type { User } from "../types";

interface AuthState {
  jwtToken: string | null;
  user: User | null;
  isAuthenticated: boolean;
}

interface AuthContextType extends AuthState {
  loginSuccess: (jwtToken: string, user: User) => void;
  logout: () => void;
}

const LOGIN_SUCCESS = "LOGIN_SUCCESS" as const;
const LOGOUT = "LOGOUT" as const;

type AuthAction = { type: typeof LOGIN_SUCCESS; payload: { jwtToken: string; user: User } } | { type: typeof LOGOUT};

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used inside AuthProvider");
  return context;
};

const authReducer = (prevState: AuthState, action: AuthAction): AuthState => {
  switch (action.type) {
    case LOGIN_SUCCESS:
      return {
        ...prevState,
        jwtToken: action.payload.jwtToken,
        user: action.payload.user,
        isAuthenticated: true,
      };
    case LOGOUT:
      return {
        ...prevState,
        jwtToken: null,
        user: null,
        isAuthenticated: false,
      }
  }
};

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const initialAuthState = ((): AuthState => {
    try {
      const jwtToken = localStorage.getItem("jwtToken");
      const user = localStorage.getItem("user");
      if (jwtToken && user) {
        return {
          jwtToken,
          user: JSON.parse(user),
          isAuthenticated: true,
        };
      }
    } catch (error) {
      console.error("Failed to load from localStorage", error);
    }
    return {
      jwtToken: null,
      user: null,
      isAuthenticated: false,
    }
  })();

  const [authState, dispatch] = useReducer(authReducer, initialAuthState);

  useEffect(() => {
    try {
      if (authState.isAuthenticated) {
        localStorage.setItem("jwtToken", authState.jwtToken!);
        localStorage.setItem("user", JSON.stringify(authState.user));
      } else {
        localStorage.removeItem("jwtToken");
        localStorage.removeItem("user");
      }
    } catch (error) {
      console.error("Failed to save to localStorage:", error);
    }
  }, [authState]);

  const loginSuccess = (jwtToken: string, user: User): void => {
    dispatch({ type: LOGIN_SUCCESS, payload: { jwtToken, user} });
  };

  const logout = (): void => {
    dispatch({ type: LOGOUT });
  };

  return (
    <AuthContext.Provider
      value={{
        jwtToken: authState.jwtToken,
        user: authState.user,
        isAuthenticated: authState.isAuthenticated,
        loginSuccess,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
