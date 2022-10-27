import {useStateMachine} from "little-state-machine";
import styled from "styled-components";
import {Link} from "react-router-dom";
import React from "react";

const Wrapper = styled.div`
    display: flex;
    flex-direction: row;
    height: 20%;
    width: 100%
`
const NavElement = styled.div`
    background-color: white;
    text-align: center;
    flex-grow: 1
`

function NavBar() {
    const {state} = useStateMachine()
    React.useEffect(() => {

    })

    return (
        <Wrapper>
            <NavElement>
                <Link to={"/"}>Home</Link>
            </NavElement>
            {
                state?.jwt ? <NavElement><Link to={"/roles"}> Roles </Link></NavElement> : <div/>
            }
        </Wrapper>
    )

}
export default NavBar