import {BrowserRouter, Link, Route, Routes} from "react-router-dom";
import MainPage from "./MainPage/MainPage";
import OauthRedirect from "./OauthRedirect";
import React from "react";
import {createStore, StateMachineProvider} from "little-state-machine";
import {DevTool} from "little-state-machine-devtools";
import RolePage from "./RolePage/RolePage";
import {AxiosProvider} from "./util/AxiosProvider";
import NavBar from "./shared/components/NavBar";


createStore({
    jwt: undefined,
    userDetails: undefined,
    accessToken: undefined
}, {
    name: 'nolebot-lsm-store',
    storageType: sessionStorage,
    persist: 'beforeUnload'
})

function App() {
    return (
        <StateMachineProvider>
            <AxiosProvider>
                <BrowserRouter>
                    <div>
                        <NavBar></NavBar>
                        {process.env.NODE_ENV !== 'production' && <DevTool />}
                        {/* A <Switch> looks through its children <Route>s and
                renders the first one that matches the current URL. */}
                        <Routes>
                            <Route path="/auth/redirect" element={<OauthRedirect />} />
                            <Route path="/" element={<MainPage />} />
                            <Route path={"/roles"} element={<RolePage/>}/>
                        </Routes>
                    </div>
                </BrowserRouter>
            </AxiosProvider>
        </StateMachineProvider>

    )
}

export default App;