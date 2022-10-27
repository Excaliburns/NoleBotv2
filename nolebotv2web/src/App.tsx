import {BrowserRouter, Link, Route, Routes} from "react-router-dom";
import MainPage from "./MainPage/MainPage";
import OauthRedirect from "./OauthRedirect";
import React from "react";
import {createStore, StateMachineProvider} from "little-state-machine";
import {DevTool} from "little-state-machine-devtools";
import RolePage from "./RolePage/RolePage";
import {AxiosProvider} from "./util/AxiosProvider";
import NavBar from "./shared/components/NavBar";
import {createTheme, CssBaseline, ThemeProvider} from "@mui/material";


createStore({
    jwt: undefined,
    userDetails: undefined,
    accessToken: undefined
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
                            {/* A <Switch> looks through its children <Route>s and
                    renders the first one that matches the current URL. */}
                            <Routes>
                                <Route path="/auth/redirect" element={<OauthRedirect />} />
                                <Route path="/" element={<MainPage />} />
                                <Route path={"/roles"} element={<RolePage/>}/>
                            </Routes>
                        </div>
                    </ThemeProvider>
                </BrowserRouter>
            </AxiosProvider>
        </StateMachineProvider>

    )
}

export default App;