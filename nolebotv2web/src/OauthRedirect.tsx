import React from "react";
import {GlobalState, useStateMachine} from "little-state-machine";
import {useLocation, useNavigate} from "react-router";
import Spinner from "./shared/components/Spinner";
import {useAxios} from "./util/AxiosProvider";

function updateJwt(state: GlobalState, payload: string) {
    return {
        ...state,
        jwt: payload
const updateUserToken = (state: GlobalState, newToken: string) => ({
    ...state,
    accessToken: newToken
});

const updateUserDetails = (state: GlobalState, payload: APIUser) => (
    {
    ...state,
    userDetails: {
        ...state.userDetails,
        ...payload

}

function OauthRedirect() {
    const location = useLocation();
    const clientCode = new URLSearchParams(location.search).get("code");

    const {actions, state} = useStateMachine({
        updateJwt,
        updateUserDetails,
        updateUserToken
    });
    const axios = useAxios();
    const navigate = useNavigate();


    React.useEffect(() => {
        if (clientCode) {
            axios.post('/oauth/discord', {
                clientCode: clientCode
            console.log(clientCode)
            axiosNolebotInstance.post('/login', {
                auth_token: clientCode
            }).then( response => {
                axiosNolebotInstance.defaults.headers.common['Authorization'] = "Bearer " + response.data.access_token
                axiosNolebotInstance.get("/oauth/token").then(response => {
                    console.log(response.data)
                    actions.updateUserToken(response.data)
                })
            })
                .then(response => {
                    actions.updateJwt(response.data);
                    console.log(response.data);
                    navigate('/');
                })

        }

    }, [clientCode])

    React.useEffect(() => {
        if (state?.accessToken) {
            axiosDiscordInstance.get('/users/@me', {
                headers: {
                    Authorization: `Bearer ${state.accessToken}`
                }
            })
            .then ( response => {
                actions.updateUserDetails(response.data)
                navigate('/');
            })
        }
    }, [state?.accessToken])


    return (
        <div>
            <Spinner/>
            Doing some stuff.... You will be redirected shortly.
        </div>
    )
}

export default OauthRedirect;