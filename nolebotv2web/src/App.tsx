import { BrowserRouter, Link, Route, Router, Switch } from "react-router-dom";
import MainPage from "./MainPage";
import OauthRedirect from "./OauthRedirect";
import React from "react";
import { DiscordUser } from "./entities/DiscordUser";
import { createStore, GlobalState, StateMachineProvider, useStateMachine } from "little-state-machine";


createStore({
    userDetails: undefined
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
                    <Switch>
                        <Route path="/auth/redirect">
                            <OauthRedirect />
                        </Route>
                        <Route path="/">
                            <MainPage />
                        </Route>
                    </Switch>
                </div>
            </BrowserRouter>
        </StateMachineProvider>
    )
}

export default App;