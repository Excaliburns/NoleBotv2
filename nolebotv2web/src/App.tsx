import { BrowserRouter, Link, Route, Routes } from "react-router-dom";
import MainPage from "./MainPage/MainPage";
import OauthRedirect from "./OauthRedirect";
import React from "react";
import { createStore, StateMachineProvider } from "little-state-machine";


createStore({
    userDetails: undefined,
    userToken: undefined
},{
    name: 'nolebot-lsm-store',
    storageType: sessionStorage
})
function App() {
    return (
        <StateMachineProvider>
            <BrowserRouter>
                <div>
                    <nav>
                        <ul>
                            <li>
                                <Link to="/">Home</Link>
                            </li>
                        </ul>
                    </nav>

                    {/* A <Switch> looks through its children <Route>s and
            renders the first one that matches the current URL. */}
                    <Routes>
                        <Route path="/auth/redirect" element={<OauthRedirect />} />
                        <Route path="/" element={<MainPage />} />
                    </Routes>
                </div>
            </BrowserRouter>
        </StateMachineProvider>
    )
}

export default App;