import { BrowserRouter, Link, Route, Router, Switch } from "react-router-dom";
import MainPage from "./MainPage";
import OauthRedirect from "./OauthRedirect";
import React from "react";
import { DiscordUser } from "./entities/DiscordUser";

function App() {
    const [user, setUser] = React.useState<DiscordUser | undefined>(undefined);

    return (
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
                        <OauthRedirect setUserData={setUser}/>
                    </Route>
                    <Route path="/">
                        <MainPage user={user}/>
                    </Route>
                </Switch>
            </div>
        </BrowserRouter>
    )
}

export default App;