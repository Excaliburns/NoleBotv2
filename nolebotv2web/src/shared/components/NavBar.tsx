import {useStateMachine} from "little-state-machine";
import styled from "styled-components";
import {Link} from "react-router-dom";
import React from "react";
import {Avatar, Box, Button, createTheme, useTheme} from "@mui/material";
import {useNavigate} from "react-router";

function getNavButton(link: string, name: String): JSX.Element{
    return (
        <Button variant={"contained"} href={link} sx={{
            flex: 1,
            borderRadius: 0,
            boxShadow: 0
        }}>{name}</Button>
    )
}

function NavBar() {
    const {state} = useStateMachine()
    const theme = useTheme()


    let navButtons = [getNavButton("/", "Home")]
    if (state?.jwt) {
        navButtons.push(getNavButton("/roles", "Game Managers"))
        navButtons.push(getNavButton("/test", "Test"))
    }

    return (
        <Box sx={{
            width: "100%",
            height: "10%",
            display: "flex",
            backgroundColor: theme.palette.primary.main
        }}>
            <Avatar alt={"FSU Esports Logo"} src={"logo192.png"} sx={{
                marginLeft: "0.25%"
            }}/>
            <Box sx={{
                flex: 20,
                display: "flex"
            }}>
                {navButtons}
            </Box>
            <Box sx={{
                display: "flex",
                flex: 30,
                justifyContent: "flex-end"
            }}>
                {!state?.jwt ? <Button variant={"contained"} href={"/login"} sx={{boxShadow: 0}}>Login</Button> : <></>}
                <Avatar alt={"User Profile"} src={""} sx={{
                    marginX: "0.25%"
                }}/>
            </Box>
        </Box>
    )

}
export default NavBar