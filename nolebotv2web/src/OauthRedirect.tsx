import React from "react";
import { useLocation } from "react-router";
import axiosInstance from "./uril/AxiosInstance";

function OauthRedirect() {
    const location = useLocation();

    React.useEffect( () => {
        const clientCode = new URLSearchParams(location.search).get("code");
        if (clientCode) {
            axiosInstance.post('/oauth/discord', {
                clientCode: clientCode
            })
                .then( response => {
                    console.log(response);
                })
        }

    }, [])


    return (
        <div>
        </div>
    )
}

export default OauthRedirect;