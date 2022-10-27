import React from 'react';
import '../../App.css';
import styled from "styled-components";
import {useStateMachine} from "little-state-machine";
import GuildListing from "./GuildListing";
import NavBar from "../../shared/components/NavBar";
import {useNavigate} from "react-router";

const Wrapper = styled.div`
  display: flex;
  align-items: center;
  flex-flow: column;
  padding: 2rem;
 
  height: 100%;
  color: white;
`


const discordOauthUri = process.env.REACT_APP_DISCORD_OAUTH_URI

function TestPage() {
    const {state} = useStateMachine();
    const navigate = useNavigate();

    console.log(process.env);

    return (
        <Wrapper>
            {
                state?.jwt ? <GuildListing />
                :
                navigate("/")
            }
        </Wrapper>
    );
}

export default TestPage;
