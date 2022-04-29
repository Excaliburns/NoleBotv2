import {BrowserRouter, Link, Route, Routes} from "react-router-dom";
import MainPage from "./MainPage/MainPage";
import OauthRedirect from "./OauthRedirect";
import React from "react";
import {createStore, StateMachineProvider, useStateMachine} from "little-state-machine";
import RolePage from "./RolePage/RolePage";
import axios from "axios";
import {AxiosProvider} from "./util/AxiosProvider";


createStore({
    jwt: undefined,
    userDetails: undefined,
    accessToken: undefined
}, {
    name: 'nolebot-lsm-store',
    storageType: sessionStorage
})
function App() {
    return (
        <StateMachineProvider>
            <AxiosProvider>
                <BrowserRouter>
                    <div>
                        <nav>
                            <ul>
                                <li>
                                    <Link to="/">Home</Link>
                                </li>
                                <li>
                                    <Link to={"/roles"}>Roles</Link>
                                </li>
                            </ul>
                        </nav>

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