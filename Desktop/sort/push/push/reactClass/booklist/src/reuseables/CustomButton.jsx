const CustomButton =(props) => {

    const {style, onClick, textcontent, type} = props;
    return (
        <button onClick={onClick} className={style} type= {type}>{textcontent}</button>
    )
}
export default CustomButton;