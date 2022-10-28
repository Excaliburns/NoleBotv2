import {BrowserRouter, Link, Route, Routes} from "react-router-dom";
import TestPage from "./pages/TestPage/TestPage";
import OauthRedirect from "./OauthRedirect";
import React from "react";
import {createStore, StateMachineProvider} from "little-state-machine";
import {DevTool} from "little-state-machine-devtools";
import {AxiosProvider} from "./util/AxiosProvider";
import NavBar from "./shared/components/NavBar";
import {createTheme, CssBaseline, ThemeProvider} from "@mui/material";
import HomePage from "./pages/HomePage/HomePage";
import LoginPage from "./pages/LoginPage/LoginPage";
import GameManagerPage from "./pages/GameManagerPage/GameManagerPage";
import AdminPage from "./pages/AdminPage/AdminPage";


createStore({
    jwt: undefined,
    userDetails: undefined,
    guildUserDetails: undefined
}, {
    name: 'nolebot-lsm-store',
    storageType: sessionStorage,
    persist: 'beforeUnload'
})

const baseTheme = createTheme({
    palette: {
        primary: {
            main: "#782f40",
            light: "#aa5b6b",
            dark: "#48001a",
            contrastText: "#ffffff"
        },
        secondary: {
            main: "#ceb888",
            light: "#ffeab8",
            dark: "#9c885b",
            contrastText: "#000000"
        },
        background: {
            default: "#36393f"
        }

    }
})

function App() {
    return (
        <StateMachineProvider>
            <AxiosProvider>
                <BrowserRouter>
                    <ThemeProvider theme={baseTheme}>
                        <CssBaseline/>
                        <div>
                            <NavBar></NavBar>
                            {process.env.NODE_ENV !== 'production' && <DevTool />}
                            {/* A <Switch> looks through its children <Route>s and
                    renders the first one that matches the current URL. */}
                            <Routes>
                                <Route path="/auth/redirect" element={<OauthRedirect />} />
                                <Route path="/" element={<HomePage />} />
                                <Route path="/login" element={<LoginPage/>} />
                                <Route path="/gm" element={<GameManagerPage/>}/>
                                <Route path="/admin" element={<AdminPage/>}/>
                                <Route path={"/test"} element={<TestPage/>} />
                            </Routes>
                        </div>
                    </ThemeProvider>
                </BrowserRouter>
            </AxiosProvider>
        </StateMachineProvider>

    )
}

export default App;