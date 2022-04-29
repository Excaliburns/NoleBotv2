import React from 'react';
import logo from '../logo.svg';
import '../App.css';
import styled from "styled-components";
import { DiscordAccessToken } from "../entities/DiscordAccessToken";
import { useStateMachine } from "little-state-machine";
import GuildListing from "./GuildListing";

const Wrapper = styled.div`
  display: flex;
  align-items: center;
  flex-flow: column;
  padding: 2rem;
 
  height: 100%;
  color: white;
`

const DiscordButton = styled.button`
  display: inline-block;
  font-family: Whitney, "Open Sans", Helvetica, sans-serif;
  font-weight: 400;
  font-size: 11pt;
  border-radius: 3px;
  cursor: pointer;
  height: 45px;
  width: 250px;
  box-shadow: 0 2px 6px 0 rgba(0, 0, 0, 0.2);
  border: 2px solid #7289DA;
  background-color: #7289DA;
  color: white;
`
const discordOauthUri = process.env.REACT_APP_DISCORD_OAUTH_URI

function MainPage() {
    const {state} = useStateMachine();

    console.log(process.env);

    return (
        <Wrapper>
            {
                state?.jwt ? <GuildListing />
                :
                <>
                    <a href={discordOauthUri}>
                        <DiscordButton>Log in with Discord</DiscordButton>
                    </a>
                </>
            }
        </Wrapper>
    );
}

export default MainPage;
