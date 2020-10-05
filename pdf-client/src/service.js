export const service = {
    upload: (formData)=>  fetch('/api/pdf/upload', {
        method: 'post',
        body: formData
    }),
    getAll: ()=> fetch('/api/pdf/all').then(response=> response.json()),
    getById: (id)=> fetch(`/api/pdf/get/${id}`, {
        method: 'GET',
    }).then(res => res.arrayBuffer())
}