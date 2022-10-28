import {Box} from "@mui/material";
import React from "react";
import {GlobalState, useStateMachine} from "little-state-machine";
import {DiscordUser, GuildUser} from "../../entities/JavaGenerated";
import axios, {AxiosResponse} from "axios";
import {useAxios} from "../../util/AxiosProvider";
import {APIGuild} from "discord-api-types";

const updateUserDetails = (state: GlobalState, payload: DiscordUser) => ({
    ...state,
    userDetails: {
        ...state.userDetails,
        ...payload
    }
});
const updateGuildUserDetails = (state: GlobalState, payload: GuildUser) => ({
    ...state,
    guildUserDetails: {
        ...state.guildUserDetails,
        ...payload
    }
});

export default function HomePage() {
    const {actions, state} = useStateMachine({
        updateUserDetails,
        updateGuildUserDetails
    });
    const axios = useAxios()

    React.useEffect(() => {
        axios.get('/user/info')
            .then((response) => {
                console.log(response.data)
                actions.updateUserDetails(response.data)
            })
    }, [state?.jwt])

    React.useEffect(() => {
        axios.get('/user/guilds')
            .then( (response) => {
                const fsu = response.data.find( (each: APIGuild) => each.id === process.env.REACT_APP_MAIN_SERVER_ID );
                if (fsu) {
                    axios.post(`guilds/${process.env.REACT_APP_MAIN_SERVER_ID}/me`)
                        .then((r: AxiosResponse<GuildUser>) => {
                            actions.updateGuildUserDetails(r.data)
                        })
                }
            })
    }, [state.jwt])

    return (
        <Box>
            Welcome to Esports at FSU's website! Work in progress.
        </Box>
    )
}