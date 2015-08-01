var dragData = null; 
function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
	ev.dataTransfer.effectAllowed = 'move';
    ev.dataTransfer.setData("text", ev.target.id);
    dragData = ev.target.id;
    /*
    dragData = ev.dataTransfer.getData("text");
	ev.dataTransfer.effectAllowed = 'move';
	ev.dataTransfer.setData('text/html', this.innerHTML);
	*/
}

function drop(ev) {
    ev.preventDefault();
    if (ev.stopPropagation) {
		ev.stopPropagation();
	}
   // dragData.innerHTML = this.innerHTML;
    var data = ev.dataTransfer.getData("text");
    dragData.text = data;
    ev.target.appendChild(document.getElementById(data));
  
    
	//if (dragData != this) {
	//	dragData.innerHTML = this.innerHTML;
	//	this.innerHTML = ev.dataTransfer.getData('text/html');
	//} 
	//return false;

	
}	