import {useStateMachine} from "little-state-machine";
import styled from "styled-components";
import {Link} from "react-router-dom";
import React from "react";
import {Avatar, Box, Button, createTheme, Menu, MenuItem, useTheme} from "@mui/material";
import {useNavigate} from "react-router";

function getNavButton(link: string, name: String, relSize: number): JSX.Element{
    return (
        <Button variant={"contained"} href={link} sx={{
            flex: 1,
            flexGrow: relSize,
            borderRadius: 0,
            boxShadow: 0,
        }}>{name}</Button>
    )
}

function NavBar() {
    const {state} = useStateMachine()
    const theme = useTheme()
    const [officerMenuAnchorEl, setOfficerMenuAnchorEl] = React.useState<null | HTMLElement>(null);
    const navigate = useNavigate()
    let isOpen = Boolean(officerMenuAnchorEl)
    let officerButton = <Button
        variant={"contained"} sx={{
        flex: 1,
        flexGrow: 2,
        borderRadius: 0,
        boxShadow: 0,
    }}
    onClick={(event) => {
        setOfficerMenuAnchorEl(event.currentTarget)
    }}>{"Officers"}</Button>


    let navButtons = [getNavButton("/", "Home", 1)]
    if (state?.jwt) {
        if(state?.guildUserDetails?.isGameManager) {
            navButtons.push(getNavButton("/roleassign", "Game Managers", 2))
        }
        if (state?.guildUserDetails?.isAdmin) {
            navButtons.push(officerButton)
        }
        navButtons.push(getNavButton("/test", "Test", 1))
    }

    return (
        <>
            <Box sx={{
                width: "100vw",
                height: "6vh",
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
            <Menu
                open={isOpen}
                anchorEl={officerMenuAnchorEl}
                onClose={() => setOfficerMenuAnchorEl(null)}>
                <MenuItem sx={{
                    width: officerMenuAnchorEl?.offsetWidth
                }} onClick={() => navigate("/roleassign")}>Roles</MenuItem>
                <MenuItem sx={{
                    width: officerMenuAnchorEl?.offsetWidth
                }} onClick={() => navigate("/categories")}>Categories</MenuItem>
            </Menu>
        </>
    )

}
export default NavBar