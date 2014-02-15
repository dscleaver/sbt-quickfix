set errorformat=%E\ %#[error]\ %#%f:%l:\ %m,%-Z\ %#[error]\ %p^,%-G\ %#[error]\ %m
set errorformat+=%W\ %#[warn]\ %#%f:%l:\ %m,%-Z\ %#[warn]\ %p^,%-G\ %#[warn]\ %m
set errorformat+=%C\ %#%m

function! sbtquickfix#FindSCMDir()
  let filedir = expand('%:p:h')
  let dir = finddir('.git', filedir . ';')
  let dir = substitute(dir, '/.git', '', '')
  return dir
endfunction

function! sbtquickfix#LoadQuickFix()
  let dir = sbtquickfix#FindSCMDir()
  let tempfile = tempname()
  let cmd = 'ls -t $(find ' . dir . ' -name sbt.quickfix) | xargs cat > ' . tempfile
  call system(cmd)
  exec ':cf ' . tempfile
endfunction

let g:quickfix_load_mapping="<leader>qf"
let g:quickfix_next_mapping="<leader>qn"

function! s:MakeMappings()
  if g:quickfix_load_mapping != ""
    exec ":nnoremap <silent> <buffer> " . g:quickfix_load_mapping . " :call sbtquickfix#LoadQuickFix()<cr>"
  endif
  if g:quickfix_next_mapping != ""
    exec ":nnoremap <silent> <buffer> " . g:quickfix_next_mapping . " :cn<cr>"
  endif
endfunction

augroup SbtVim
  autocmd!
  autocmd BufRead *.scala call s:MakeMappings()
augroup END

