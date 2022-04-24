import React, { Dispatch, SetStateAction } from "react";
import { axiosDiscordInstance, axiosNolebotInstance } from "./util/AxiosNolebotInstance";
import { AccessToken } from "./entities/AccessToken";
import { GlobalState, useStateMachine } from "little-state-machine";
import { APIUser } from "discord-api-types";
import { useLocation, useNavigate } from "react-router";
import Spinner from "./shared/components/Spinner";

const updateUserToken = (state: GlobalState, payload: AccessToken) => ({
    ...state,
    userToken: {
        ...state.userToken,
        ...payload
    }
});

const updateUserDetails = (state: GlobalState, payload: APIUser) => (
    {
    ...state,
    userDetails: {
        ...state.userDetails,
        ...payload
    }
})

function OauthRedirect() {
    const location = useLocation();
    const clientCode = new URLSearchParams(location.search).get("code");

    const { actions, state } = useStateMachine({
        updateUserToken,
        updateUserDetails
    });

    const navigate = useNavigate();

    React.useEffect( () => {
        if (clientCode) {
            axiosNolebotInstance.post('/oauth/discord', {
                clientCode: clientCode
            })
                .then( response => {
                    const data: {
                        access_token: string,
                        expires_in: number,
                        refresh_token: string,
                        scope: string[],
                        token_type: string
                    } = response.data ;

                    const tokenResponse: AccessToken = {
                        access_token: data.access_token,
                        expires_in: data.expires_in,
                        refresh_token: data.refresh_token,
                        scope: data.scope[0].split(' '),
                        token_type: data.token_type
                    }

                    actions.updateUserToken(tokenResponse)
                })
        }

    }, [clientCode])

    React.useEffect(() => {
        if (state?.userToken) {
            axiosNolebotInstance.post(
                "/xd", {"token": state.userToken.access_token}
            ).then((r) => {
                console.log(r.headers)
            })
        }
    }, [state?.userToken])


    return (
        <div>
            <Spinner />
            Doing some stuff.... You will be redirected shortly.
        </div>
    )
}

export default OauthRedirect;