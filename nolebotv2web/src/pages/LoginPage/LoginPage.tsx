import {Box} from "@mui/material";
import styled from "styled-components";


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

const discordOauthUri: string | undefined = process.env.REACT_APP_DISCORD_OAUTH_URI

export default function LoginPage() {
    return(
        <div style={{
            textAlign: "center",
            margin: "2.5%"
        }}>
            <a href={discordOauthUri}>
                <DiscordButton>Login with Discord</DiscordButton>
            </a>
        </div>
    )
}