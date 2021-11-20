import styled from "styled-components";

const Spinner = styled.div<{zIndexOverride?: number}>`
  position: absolute;
  z-index: ${(props) => props.zIndexOverride ?? 30};

  left: 50%;
  top: 50%;
  margin: auto;
  width: 100px;
  height: 100px;
  animation: rotation .6s infinite linear;
  border: 6px solid #7289DA;
  border-top: 6px solid rgba(0, 0, 0, 0.2);
  border-radius: 100%;

  @keyframes rotation {
    from {
      transform: rotate(0deg)
    }
    to {
      transform: rotate(359deg)
    }
  }
`

export default Spinner;